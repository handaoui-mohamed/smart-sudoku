from app import app, db, socketio
from flask_socketio import emit
from flask import abort, request, jsonify
from werkzeug.datastructures import MultiDict
from models import Grid
from forms import NewGridForm
from random import randint
from datetime import datetime


@app.route('/api/grids')
def get_grids():
    grids = Grid.query.all()
    return jsonify([element.to_json() for element in grids])


@app.route('/api/grids/random')
def get_random_grid():
    grids_count = Grid.query.count()
    grid = Grid.query.get(randint(1, grids_count))
    if not grid:
        abort(404)
    return jsonify(grid.to_json())


@app.route('/api/grids/today')
def get_today_grid():
    now = datetime.now()
    currentDay = (now - datetime(now.year, 1, 1, 0, 0)).days

    grid = Grid.query.get(currentDay)
    if not grid:
        abort(404)
    return jsonify(grid.to_json())


@app.route('/api/grids', methods=['POST'])
def add_grid():
    data = request.get_json(force=True)
    form = NewGridForm(MultiDict(mapping=data))
    if form.validate():
        configuration = data.get('configuration')
        solution = data.get('solution')
        grid = Grid(configuration=configuration,
                    solution=solution)
        db.session.add(grid)
        db.session.commit()
        emit('new_grid', grid.to_json())
        return jsonify({'success': 'true'}), 201
    return jsonify({"form_errors": form.errors}), 400


@socketio.on('check_new_grid')
def check_update(last_update):
    # emit lastest config
    grids = Grid.query\
        .filter(Grid.date < datetime.strptime(last_update, "%a, %d %b %Y %H:%M:%S %Z"))\
        .order_by(Grid.id.desc()).all()
    if(grids is not None):
        emit('new_grids', [grid.to_json() for grid in grids])

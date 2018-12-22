from app import app
from flask import abort, request, jsonify
from models import Grid
from random import randint


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

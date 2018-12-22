from app import db, app
from app.user.models import User
from app.user.forms import RegistrationForm, LoginForm
from flask import abort, request, jsonify
from werkzeug.datastructures import MultiDict


@app.route('/api/users')
def get_users():
    users = User.query.all()
    return jsonify([element.to_json() for element in users])


@app.route('/api/users', methods=['POST'])
def new_user():
    data = request.get_json(force=True)
    form = RegistrationForm(MultiDict(mapping=data))
    if form.validate():
        username = data.get('username')
        password = data.get('password')
        first_name = data.get('first_name')
        last_name = data.get('last_name')
        user = User(username=username.lower(),
                    first_name=first_name.lower(),
                    last_name=last_name.lower())
        user.hash_password(password)
        db.session.add(user)
        db.session.commit()
        return jsonify({'success': 'true'}), 201
    return jsonify({"form_errors": form.errors}), 400


@app.route('/api/login', methods=["POST"])
def login():
    data = request.get_json(force=True)
    form = LoginForm(MultiDict(mapping=data))
    if form.validate():
        username = data.get('username').lower()
        password = data.get('password')
        user = User.query.filter_by(username=username).first()
        if not user or not user.verify_password(password):
            return jsonify({"form_errors": "Nom d\'utilisateur ou mot de passe incorrect"}), 400
        return jsonify(user.to_json())
    return jsonify({"form_errors": form.errors}), 400

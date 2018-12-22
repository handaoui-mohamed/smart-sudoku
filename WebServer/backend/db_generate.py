#!flask/bin/python
from app import db
from app.user.models import User
from app.grid.models import Grid
import json

db.drop_all()
db.create_all()

# creation of all configs from grid_configs.json
with open("data/grid_configs.json", "r") as grids_json:
    grids = json.load(grids_json)

for grid in grids:
    db.session.add(
        Grid(solution=grid["solution"], configuration=grid["configuration"]))
db.session.commit()


# creation of all users from users.json
with open("data/users.json", "r") as users_json:
    users = json.load(users_json)

for user in users:
    new_user = User(username=user["username"], first_name=user["first_name"], last_name=user["last_name"])
    new_user.hash_password(user["password"])
    db.session.add(new_user)
db.session.commit()

# -*- coding: utf-8 -*-
#!/usr/bin/env python
import os
from flask import Flask, url_for
from flask_sqlalchemy import SQLAlchemy
from config import basedir
from flask_cors import CORS
import sys

reload(sys)
sys.setdefaultencoding('utf8')

# initialization
app = Flask(__name__, static_url_path='')
app.config.from_object('config')
# extensions
db = SQLAlchemy(app)

cors = CORS(app, resources={r"/api/*": {"origins": "*"}})
# import APIs
from app.user import views
from app.grid import views
from app.frontend import views

# import models
from app.user.models import User
from app.grid.models import Grid


# -*- coding: utf-8 -*-
from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, validators
from wtforms.validators import DataRequired, Length
from app.user.models import User


class NewGridForm(FlaskForm):
    configuration = StringField('configuration',validators=[
        DataRequired('La configuration est nécessaire'),
        Length(min=81, max=81, message="La configuration doit avoir 81 caractères")
    ])
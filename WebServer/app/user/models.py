# -*- coding: utf-8 -*-
from app import db, app
from passlib.apps import custom_app_context as pwd_context
from config import SECRET_KEY

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True)
    password_hash = db.Column(db.String)
    first_name = db.Column(db.String(50))
    last_name = db.Column(db.String(50))

    def hash_password(self, password):
        self.password_hash = pwd_context.encrypt(password)

    def verify_password(self, password):
        return pwd_context.verify(password, self.password_hash)
    
    def to_json(self):
        return {
            'id': self.id,
            'username': self.username,
            'first_name': self.first_name,
            'last_name': self.last_name
        }

    def __repr__(self):
        return '<User N=%s username=%s>' % (self.id, self.username)

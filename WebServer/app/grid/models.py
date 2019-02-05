# -*- coding: utf-8 -*-
from app import db, app
from datetime import datetime

class Grid(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    configuration = db.Column(db.String(81))
    solution = db.Column(db.String(81))
    date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)

    def to_json(self):
        return {
            'id': self.id,
            'configuration': self.configuration,
            'solution': self.solution
        }

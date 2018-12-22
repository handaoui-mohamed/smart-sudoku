# -*- coding: utf-8 -*-
from app import db, app


class Grid(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    configuration = db.Column(db.String(81))
    solution = db.Column(db.String(81))

    def to_json(self):
        return {
            'id': self.id,
            'configuration': self.configuration,
            'solution': self.solution
        }

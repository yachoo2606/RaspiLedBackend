import sqlalchemy as sqla
import sqlalchemy.orm as sqlaOrm

engine = sqla.create_engine('sqlite:///database.db', echo=True, connect_args={'check_same_thread': False})

base = sqlaOrm.declarative_base()


class DBModel:
    class User(base):
        __tablename__ = 'users'

        id = sqla.Column(sqla.Integer, primary_key=True)
        name = sqla.Column(sqla.String)
        login = sqla.Column(sqla.String, unique=True)
        phoneNumber = sqla.Column(sqla.String, unique=True)
        password = sqla.Column(sqla.String)
        role = sqla.Column(sqla.Boolean, default=False)

        warning = sqlaOrm.relationship("Warnings")
        reservation = sqlaOrm.relationship("Reservations")

        def __repr__(self):
            return f"<User(name={self.name}, login={self.login}, phoneNumber={self.phoneNumber}, password={self.password}, role={self.role})>"

        def toDict(self):
            return dict(id=self.id, name=self.name, login=self.login, phoneNumber=self.phoneNumber,
                        password=self.password, role=self.role)

    class Warnings(base):
        __tablename__ = 'warnings'

        id = sqla.Column(sqla.Integer, primary_key=True)
        userId = sqla.Column(sqla.Integer, sqla.ForeignKey('users.id'))
        warning = sqla.Column(sqla.String)

        def __repr__(self):
            return f"<Warning (id={self.id}, userId={self.userId}, warning={self.warning})>"

        def toDict(self):
            return dict(id=self.id, userId=self.userId, warning=self.warning)

    class Reservations(base):
        __tablename__ = 'reservations'

        id = sqla.Column(sqla.Integer, primary_key=True)
        userId = sqla.Column(sqla.Integer, sqla.ForeignKey('users.id'))
        start_date = sqla.Column(sqla.DATETIME, nullable=False)
        end_date = sqla.Column(sqla.DATETIME, nullable=False)
        content = sqla.Column(sqla.String, nullable=False)

        #Format "text - *" or "path - *"
        def __repr__(self):
            return f"<Reservation (id={self.id}, userId={self.userId}, start_date={self.start_date}, end_date={self.end_date}, content={self.content})>"

        def toDict(self):
            return dict(id=self.id, userId=self.userId, start_date=self.start_date, end_date=self.end_date, content=self.content)


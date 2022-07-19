from flask import Flask, request, jsonify, send_file
import sqlalchemy as sqla
import sqlalchemy.orm as sqlaOrm
from DBModelClass import base, engine, DBModel
import os
import signal
import logging
from logging.handlers import RotatingFileHandler
import datetime
from glob import glob
from io import BytesIO
from zipfile import ZipFile

app = Flask(__name__)

"""========================================START OF LOGGER SECTION========================================"""

Log_Format = '%(asctime)s - %(threadName)s - %(name)s - %(levelname)s - %(message)s'

logging.basicConfig(format=Log_Format,
                    datefmt='%Y-%m-%d %H:%M:%S',
                    level=logging.ERROR)

fileHandled = RotatingFileHandler("logs/RaspiLedLOG.log",
                                  mode="a",
                                  maxBytes=1024 * 500,
                                  backupCount=1)
consoleHandler = logging.StreamHandler()
consoleHandler.setFormatter(logging.Formatter(Log_Format))
fileHandled.setFormatter(logging.Formatter(Log_Format))
rootLogger = logging.getLogger()
# rootLogger.addHandler(consoleHandler)
rootLogger.addHandler(fileHandled)

"""========================================END OF LOGGER SECTION========================================"""

Session = sqlaOrm.sessionmaker(engine)
session = Session()


@app.route('/getAllUsers')
def getAllUsers():
    users = session.query(DBModel.User).all()
    app.logger.info('Read all users')
    return jsonify([u.toDict() for u in users])


@app.route('/getAllReservations')
def getAllReservations():
    reservations = session.query(DBModel.Reservations).all()
    app.logger.info('Read all Reservations')
    return jsonify([r.toDict() for r in reservations])


@app.route('/getReservedHours')
def getReservedHours():
    hours = session.query(DBModel.Reservations).filter(
        sqla.extract('day', DBModel.Reservations.start_date) == request.args.get("day"),
        sqla.extract('month', DBModel.Reservations.start_date) == request.args.get("month"),
        sqla.extract('year', DBModel.Reservations.start_date) == request.args.get("year"),
    ).all()

    hoursToRet = ""

    for h in hours:
        hoursToRet += "{:02d}".format(h.start_date.hour)

    return hoursToRet


@app.route('/register', methods=['POST'])
def register():
    if len(session.query(DBModel.User).filter(DBModel.User.login == request.args.get('login')).all()) != 0:
        app.logger.warning('Tried to register already registered user')

        errorUser = DBModel.User()
        errorUser.login = "ERROR"
        errorUser.name = "User already exist"
        errorUser.phoneNumber = "ERROR"
        errorUser.password = "ERROR"
        errorUser.role = 0

        return jsonify(errorUser.toDict())
    else:
        toAddUser = DBModel.User()
        toAddUser.name = request.args.get("name")
        toAddUser.login = request.args.get("login")
        toAddUser.phoneNumber = request.args.get("phoneNumber")
        toAddUser.password = request.args.get("password")
        toAddUser.role = int(request.args.get("role"))

        app.logger.info('New user: ' + toAddUser.login + " registered")
        session.add(toAddUser)
        session.commit()
    return jsonify(toAddUser.toDict())


@app.route('/getReservationItem')
def getReservationItem():
    if reservationItem := session.query(DBModel.Reservations).filter(
            DBModel.Reservations.id == request.args.get('id')).one():
        if "path" in reservationItem.content[:7]:
            app.logger.warning("Admin requested actual content, " + repr(reservationItem))
            return send_file(reservationItem.content[7:], mimetype="image/gif")
        else:
            return reservationItem.content[7:]

    return "There is not reservation with given ID"


@app.route('/login', methods={'POST'})
def login():
    if logingUser := session.query(DBModel.User).filter(
            DBModel.User.login == request.args.get('login'),
            DBModel.User.password == request.args.get('password')).first():
        app.logger.info('User: ' + logingUser.login + " logged")
        return jsonify(logingUser.toDict())
    app.logger.info('User: ' + request.args.get('login') + " logged failed")

    errorUser = DBModel.User()
    errorUser.login = "ERROR"
    errorUser.name = "No User found"
    errorUser.phoneNumber = "ERROR"
    errorUser.password = "ERROR"
    errorUser.role = 0

    return jsonify(errorUser.toDict())


@app.route('/addWarning', methods={'POST'})
def addWarning():
    toAddWarning = DBModel.Warnings()
    toAddWarning.userId = request.args.get('userId')
    toAddWarning.warning = request.args.get('warning')

    session.add(toAddWarning)
    session.commit()
    app.logger.info('added Warning for user: ' + toAddWarning.userId)
    return "Warning added"


@app.route('/addReservation', methods={'POST'})
def addReservation():
    toAddReservation = DBModel.Reservations()
    toAddReservation.userId = request.args.get('userId')
    toAddReservation.start_date = datetime.datetime.strptime(request.args.get('start_date'), '%Y-%m-%d %H:%M:%S')
    toAddReservation.end_date = datetime.datetime.strptime(request.args.get('end_date'), '%Y-%m-%d %H:%M:%S')
    contentType = request.args.get('type')
    if contentType == 'text':
        toAddReservation.content = "text - " + request.args.get('content')
    else:
        image = request.files.get('imagefile', '')
        nameDate = datetime.datetime.now()
        nameDate = str(nameDate.strftime('%Y-%m-%d %H-%M-%S-%f')) + ".png"
        image.save(os.path.join(app.root_path + "/Media", nameDate))
        image.close()
        toAddReservation.content = "path - " + f"Media/{nameDate}"
    session.add(toAddReservation)
    session.commit()
    app.logger.info('added Reservation: ' + repr(toAddReservation))
    return toAddReservation.toDict()


@app.route('/removeReservation', methods={'POST'})
def removeReservation():
    if reservationItem := session.query(DBModel.Reservations).filter(
            sqla.extract('day', DBModel.Reservations.start_date) == request.args.get("day"),
            sqla.extract('month', DBModel.Reservations.start_date) == request.args.get("month"),
            sqla.extract('year', DBModel.Reservations.start_date) == request.args.get("year"),
            sqla.extract('hour', DBModel.Reservations.start_date) == request.args.get("hour")
    ).one_or_none():

        NoneType = type(None)
        if type(reservationItem) == NoneType:
            return "Reservation not exist"

        session.delete(reservationItem)
        app.logger.warning("Admin deleted, " + repr(reservationItem))
        session.commit()
        return "Reservation deleted"


@app.route('/getUsersReservations')
def getUsersReservations():
    if reservationItems := session.query(DBModel.Reservations).filter(
            DBModel.Reservations.userId == request.args.get('userId')).all():
        print(f"[getUsersReservations] Znaleziono: {len(reservationItems)}")

    files = []
    text = []
    for re in reservationItems:
        if "text" not in re.content:
            files.append(re.content[7:])
        else:
            text.append(re.content[7:])

    stream = BytesIO()
    with ZipFile(stream, 'w') as zf:
        with open("reservations.txt", "w") as file:
            for r in reservationItems:
                # print(str())
                file.write(str(r.toDict()) + "\n")
            file.close()
            zf.write(file.name, os.path.basename(file.name))

        for file in files:
            zf.write(file, os.path.basename(file))
        with open("Media/texts.txt", "w") as file:
            for line in text:
                file.write(line + "\n")
            file.close()
            zf.write(file.name, os.path.basename(file.name))
    stream.seek(0)

    return send_file(
        stream,
        as_attachment=True,
        download_name='archive.zip'
    )


@app.route('/getReservationsByDateHour')
def getReservationsByDateHour():
    print(f"{request.args.get('day')},{request.args.get('month')},{request.args.get('year')}")
    if reservation := session.query(DBModel.Reservations).filter(
            sqla.extract('day', DBModel.Reservations.start_date) == request.args.get("day"),
            sqla.extract('month', DBModel.Reservations.start_date) == request.args.get("month"),
            sqla.extract('year', DBModel.Reservations.start_date) == request.args.get("year"),
            sqla.extract('hour', DBModel.Reservations.start_date) == request.args.get("hour")
    ).one_or_none():
        print(f"[getReservationsByDateHour] Znaleziono: {reservation}")

    NoneType = type(None)
    if type(reservation) == NoneType:
        return "Brak"

    if user := session.query(DBModel.User).filter(
            DBModel.Reservations.userId == request.args.get("day"),
    ).one():
        print(f"[getReservationsByDateHour] Znaleziono: {user}")
    files = []
    text = []
    if "text" not in reservation.content:
        files.append(reservation.content[7:])
    else:
        text.append(reservation.content[7:])

    stream = BytesIO()
    with ZipFile(stream, 'w') as zf:
        for file in files:
            zf.write(file, os.path.basename(file))
        with open("texts.txt", "w") as file:
            for line in text:
                file.write(line + "\n")
            file.close()
            zf.write(file.name, os.path.basename(file.name))
        with open("author.txt", "w") as file:
            file.write(str(user.id) + "\n")
            file.write(str(user.name) + "\n")
            file.write(str(user.login) + "\n")
            file.write(str(user.phoneNumber) + "\n")
            file.write(str(user.password) + "\n")
            file.write(str(user.role) + "\n")
            file.close()
            zf.write(file.name, os.path.basename(file.name))
    stream.seek(0)

    return send_file(
        stream,
        as_attachment=True,
        download_name='archive.zip'
    )


@app.route('/off')
def offService():
    app.logger.info('Serverd turned off at: ' + str(datetime.datetime.now()) + " ")
    os.kill(os.getpid(), signal.SIGINT)


if __name__ == '__main__':
    base.metadata.create_all(engine)

    app.run(host='0.0.0.0', port=5000)

    session.close()

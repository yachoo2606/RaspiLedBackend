import os.path
import time
import board
import neopixel
from PIL import Image
from adafruit_pixel_framebuf import PixelFramebuffer
import sqlalchemy as sqla
import sqlalchemy.orm as sqlaOrm
from DBModelClass import base, DBModel
import datetime
import threading

pixel_pin = board.D18
pixel_width = 16
pixel_height = 16

matrix = neopixel.NeoPixel(
    pixel_pin,
    pixel_width * pixel_height,
    brightness=0.1,
    auto_write=False,
)

pixel_framebuf = PixelFramebuffer(
    matrix,
    pixel_width,
    pixel_height,
    reverse_x=True,
    rotation=1
)
engine = sqla.create_engine('sqlite:///database.db', echo=False, connect_args={'check_same_thread': False})

Session = sqlaOrm.sessionmaker(engine)
session = Session()

# content = "Witaj Testowa wiadomosc"
# content = Image.new("RGBA", (pixel_width, pixel_height))
content = ""
mode = None
change = False

noneType = type(None)
imageType = type(Image.new("RGBA", (pixel_width, pixel_height)))
strType = type(str(""))


def showText():
    global change, content, noneType, imageType, strType
    while True:
        if mode == noneType:
            pixel_framebuf.fill(0x000000)
            pixel_framebuf.display()
            break
        for i in range(6 * len(content) + pixel_width):
            pixel_framebuf.fill(0x000088)
            pixel_framebuf.pixel(2, 1, 0x00FFFF)
            pixel_framebuf.line(0, 0, pixel_width - 1, pixel_height - 1, 0x00FF00)
            pixel_framebuf.line(0, pixel_width - 1, pixel_height - 1, 0, 0x00FF00)
            pixel_framebuf.fill_rect(2, 3, 12, 10, 0x000000)
            pixel_framebuf.text(content, pixel_width - i, 4, 0xFFFF00)
            pixel_framebuf.rect(1, 2, 14, 12, 0xFF0000)
            pixel_framebuf.line(0, 2, 0, 14, 0x000088)
            pixel_framebuf.line(pixel_width - 1, 2, pixel_width - 1, 14, 0x000088)
            pixel_framebuf.display()


def showImage():
    global change, content, noneType, imageType, strType
    while True:
        print("Obrazek jest pokazywany")
        if mode == noneType:
            pixel_framebuf.fill(0x000000)
            pixel_framebuf.display()
            break
        elif mode == strType:
            break
        image = Image.new("RGBA", (pixel_width, pixel_height))

        icon = Image.open(os.path.join("Media", content[6:]))
        print(icon.width)
        print(icon.height)
        image.alpha_composite(icon.convert("RGBA"))
        pixel_framebuf.image(image.convert("RGB"))
        pixel_framebuf.display()


def main():
    global change, mode, content, noneType, imageType, strType

    while True:
        if reservationItem := session.query(DBModel.Reservations).filter(
                sqla.extract('day', DBModel.Reservations.start_date) == datetime.datetime.now().day,
                sqla.extract('month', DBModel.Reservations.start_date) == datetime.datetime.now().month,
                sqla.extract('year', DBModel.Reservations.start_date) == datetime.datetime.now().year,
                sqla.extract('hour', DBModel.Reservations.start_date) == datetime.datetime.now().hour
        ).one_or_none():
            # print(type(reservationItem))
            # print(reservationItem)
            print(reservationItem.content)
            if "text" not in reservationItem.content:
                mode = imageType
            else:
                mode = strType
            content = reservationItem.content[7:]
            print(content)

        if type(reservationItem) == noneType:
            print("None")
            mode = noneType

        if mode == noneType:
            print("Content is NoneType")
        if mode == imageType:
            print("Content is imageType")
        if mode == strType:
            print("Content is strType")
            print(strType)
        time.sleep(1)


def show():
    global mode
    while True:
        if mode == imageType:
            showImage()
        elif mode == strType:
            showText()



if __name__ == '__main__':
    mainThreat = threading.Thread(target=main)
    showingThreat = threading.Thread(target=show)
    showingThreat.start()
    mainThreat.start()

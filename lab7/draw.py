from tkinter import *
from PIL import Image

root = Tk()
root.title("Simple Graph")
root.resizable(0,0)

tag1 = "theline"

down = False

def press(event):
    global down
    down = True
    
def release(event):
    global down
    down = False

def motion(event):
    #x, y = event.x, event.y
    if down:
        c.create_oval(event.x, event.y, event.x+1, event.y+1, width=8, fill="black")

c = Canvas(root, bg="white", width=600, height= 600)
c.configure(cursor="crosshair")
c.pack()

c.bind("<Button-1>", press)
c.bind("<ButtonRelease-1>", release)

c.bind('<Motion>', motion)

def btn_callback():
    global c
    c.postscript(file="digit.eps")
    img = Image.open("digit.eps")
    img.save("digit.png", "png")

b = Button(root, text="OK", command=btn_callback)
b.pack()

root.mainloop()
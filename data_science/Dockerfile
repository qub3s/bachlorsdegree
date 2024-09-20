FROM alpine

RUN apk update && apk add python3 && apk add py3-pip
RUN pip install numpy pandas matplotlib seaborn pytest --break-system-packages
ADD . /home/
WORKDIR /home/

# Instructions

- Build rabbitmq docker image

    docker build -f Dockerfile.rabbitmq . -t rabbitmq-stomp

- Run rabbitmq and kafka

    docker-compose up

- Run Spring application
- Open index.html
- Make GET to http://localhost:8080/send/something


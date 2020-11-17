# Introduction

Minimal example for using RabbitMQ with Stomp and JWT for auth (Oauth2 plugin).

## RabbitMQ

Version 3.8 is required when using Oauth2 auth backend.
The web stomp and oauth2 plugins can be enabled like this:

    rabbitmq-plugins enable rabbitmq_web_stomp rabbitmq_auth_backend_oauth2

The oauth2 plugin needs to be configured using advanced.config:

    [
        %% Enable rabbit_auth_backend_oauth2
        {rabbit, [
            {auth_backends, [rabbit_auth_backend_oauth2, rabbit_auth_backend_internal]}
        ]},

        %% Set a resource server ID. Will require all scopes to be prefixed with `rabbitmq.`
        {rabbitmq_auth_backend_oauth2, [
            {resource_server_id, <<"rabbitmq">>},
            % Set up a legacy signing key
            {key_config, [
                {default_key, <<"legacy-token-key">>},
                {signing_keys, #{
                    <<"legacy-token-key">> =>
                        {map, #{
                            <<"alg">> => <<"HS256">>,
                            <<"value">> => <<"secr3t">>,
                            <<"kty">> => <<"MAC">>,
                            <<"use">> => <<"sig">>}
                        }
                    }
                } %% signing keys
            ]} % key_config
        ]} % rabbitmq_auth_backend_oauth2
    ].


## Auth server

A spring-boot app that generates JWTs to authorize connections.
This is an example of a decoded token:

Header:

    {
      "kid": "legacy-token-key",
      "typ": "JWT",
      "alg": "HS256"
    }

Payload:

    {
      "aud": "rabbitmq",
      "scope": [
        "rabbitmq.read:*/*/*",
        "rabbitmq.write:*/*/*",
        "rabbitmq.configure:*/*/*"
      ],
      "exp": 1605634613
    }

Some considerations:
- `kid` must match the signing_key name or not be present.
- `aud` must always be present and match the resource_server_id or be empty.
- `scope` follows a specific format, see links for reference.
- A symmetric signing key was used for simplicity, RSA is recommended.

## Client

A simple chat application fetches the token and uses stomp.js to connect to the broker.

Fetches the token from the spring-boot app and then uses it to connect:

    $.get("http://localhost:8080/auth/token", function(token) {
      startConnection(token);
    });

    function startConnection(token) {
      const stompConfig = {
        // Connect to / vhost using token
        connectHeaders: {
          host: "/",
          login: "token", // username is ignored
          passcode: token
        }, ...
      }
    }

## Links:
 - https://github.com/rabbitmq/rabbitmq-auth-backend-oauth2
 - https://www.rabbitmq.com/web-stomp.html

# Instructions

To run the application follow these steps:

- Build rabbitmq docker image

      docker build -f Dockerfile.rabbitmq . -t rabbitmq-stomp-jwt

- Run rabbitmq

      docker-compose up

- Run Spring application

      ./mvnw spring-boot:run

- Open two tabs: http://localhost:8080/
- Chat with yourself.


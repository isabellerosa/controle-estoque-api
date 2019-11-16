.PHONY: build dev prod stop clean stop-clean help

default: help

help:
	@echo ----------------------------------------------------------------------
	@echo "                      Available Commands"
	@echo ----------------------------------------------------------------------
	@echo "    > image - Build the docker image"
	@echo "    > dev - Run the api on dev mode on port 8081 with H2 database"
	@echo "    > prod - Run the api on prod mode on port 8080 with MYSQL database."
	@echo "          Make sure to configure variables on inventory.env file"
	@echo "    > stop - Stop the running containers"
	@echo "    > clean - Remove stopped containers"
	@echo "    > stop-clean - Stop and then remove containers"
	@echo "    > rmi - Remove image"

image:
	docker image build -t isabellerosa/inventory-control .

dev:
	docker container run -d -p 8081:8080 isabellerosa/inventory-control

prod:
	docker-compose -f docker-compose.yml up -d

stop:
	docker container stop $(shell docker container ls -q --filter ancestor=isabellerosa/inventory-control)

clean:
	docker container rm $(shell docker container ls -a -q --filter ancestor=isabellerosa/inventory-control)

mysql:
	docker run

stop-clean: stop clean

rmi:
	docker image rm $(shell docker image ls -q --filter reference="isabellerosa/inventory-control:*")
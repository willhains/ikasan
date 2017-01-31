build-docker:
	docker build -t ikasan/java8-tiny:latest .

push:
	docker push ikasan/java8-tiny

help:
	@echo 'Available commands: build, push'
	@echo ''
	@echo 'Commands in this Makefile are top level commands to package the final version of the app. For low level commands, see subdirectories.'
	@echo ''

.PHONY: help
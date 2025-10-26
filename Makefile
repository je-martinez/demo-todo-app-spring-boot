podman-start-deploy:
	podman compose -f docker-compose.podman.yml up -d && make deploy

podman-start:
	podman compose -f docker-compose.podman.yml up -d

podman-stop:
	podman compose -f docker-compose.podman.yml down

podman-clean:
	podman compose -f docker-compose.podman.yml down -v

podman-restart:
	podman compose -f docker-compose.podman.yml restart

podman-logs:
	podman compose -f docker-compose.podman.yml logs -f
	
docker-start-deploy:
	docker compose -f docker-compose.yml up -d && make deploy

docker-start:
	docker compose -f docker-compose.yml up -d

docker-stop:
	docker compose -f docker-compose.yml down

docker-clean:
	docker compose -f docker-compose.yml down -v

docker-restart:
	docker compose -f docker-compose.yml restart

docker-logs:
	docker compose -f docker-compose.yml logs -f

deploy:
	cd lambdas/generate-image && yarn deploy && cd ../../
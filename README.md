# Servicio Product WebFlux

Este proyecto es un servicio de ejemplo para interactuar con el servicio [Cliente](https://github.com/ccahui/web-flux-client). Está contenerizado con Docker y se integra con BD.

## Prerrequisitos

1. Asegúrate de tener Docker y Docker Compose instalados en tu máquina. Puedes descargarlos desde [Docker Hub](https://www.docker.com/products/docker-desktop).
2. Asegúrate de tener la imagen del Cliente product-webflux-client:1.0

### Construcción de la Imagen Docker y levantamiento de los servicios
1. `cd devops`
2. `docker-compose up`
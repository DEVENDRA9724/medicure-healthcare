terraform {
  required_providers {
    docker = {
      source = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {}

resource "docker_container" "terraform_test" {
  name  = "terraform-test-container"
  image = "medicure-app:latest"
  ports {
    internal = 8080
    external = 8085
  }
}

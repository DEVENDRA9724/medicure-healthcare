# create_project_files.ps1
# Run this script as Administrator in PowerShell

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Creating Medicure Healthcare Project Files" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Set base directory
$BASE_DIR = "D:\star-agile-health-care"

# Create directory structure
Write-Host "`nCreating directory structure..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "$BASE_DIR\src\main\java\com\project\staragile" | Out-Null
New-Item -ItemType Directory -Force -Path "$BASE_DIR\src\main\resources" | Out-Null
New-Item -ItemType Directory -Force -Path "$BASE_DIR\src\test\java\com\project\staragile" | Out-Null
New-Item -ItemType Directory -Force -Path "$BASE_DIR\k8s" | Out-Null
New-Item -ItemType Directory -Force -Path "$BASE_DIR\prometheus" | Out-Null
New-Item -ItemType Directory -Force -Path "$BASE_DIR\monitoring" | Out-Null
Write-Host "✓ Directory structure created" -ForegroundColor Green

# 1. Create DataLoader.java
Write-Host "`nCreating DataLoader.java..." -ForegroundColor Yellow
@'
package com.project.staragile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private MedicureRepository doctorRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (doctorRepository.count() == 0) {
            doctorRepository.save(new Doctor("MED001", "Dr. John Smith", "Cardiology", 
                    "Medcurve - New York", "+1-555-0101", "john.smith@medcurve.com", 15));
            doctorRepository.save(new Doctor("MED002", "Dr. Sarah Johnson", "Neurology", 
                    "Medcurve - New York", "+1-555-0102", "sarah.johnson@medcurve.com", 12));
            doctorRepository.save(new Doctor("MED003", "Dr. Michael Brown", "Orthopedics", 
                    "Medcurve - New York", "+1-555-0103", "michael.brown@medcurve.com", 10));
            doctorRepository.save(new Doctor("MED004", "Dr. Emily Davis", "Pediatrics", 
                    "Medcurve - New York", "+1-555-0104", "emily.davis@medcurve.com", 8));
            doctorRepository.save(new Doctor("MED005", "Dr. Robert Wilson", "Cardiology", 
                    "Medcurve - New York", "+1-555-0105", "robert.wilson@medcurve.com", 20));
            System.out.println("=== Preloaded 5 sample doctors into database ===");
        }
    }
}
'@ | Out-File -FilePath "$BASE_DIR\src\main\java\com\project\staragile\DataLoader.java" -Encoding UTF8
Write-Host "✓ DataLoader.java created" -ForegroundColor Green

# 2. Create testng.xml
Write-Host "`nCreating testng.xml..." -ForegroundColor Yellow
@'
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Medicure Healthcare Test Suite" parallel="false">
    
    <listeners>
        <listener class-name="org.testng.reporters.EmailableReporter"/>
        <listener class-name="org.testng.reporters.XMLReporter"/>
    </listeners>
    
    <test name="Doctor Service Tests" preserve-order="true">
        <classes>
            <class name="com.project.staragile.TestMedicureService"/>
        </classes>
    </test>
    
    <test name="Integration Tests">
        <classes>
            <class name="com.project.staragile.MedicureApplicationTests"/>
        </classes>
    </test>
    
</suite>
'@ | Out-File -FilePath "$BASE_DIR\testng.xml" -Encoding UTF8
Write-Host "✓ testng.xml created" -ForegroundColor Green

# 3. Create main.tf
Write-Host "`nCreating main.tf..." -ForegroundColor Yellow
@'
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_vpc" "medicure_vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name        = "medicure-vpc"
    Environment = var.environment
    Project     = "Medicure-Healthcare"
  }
}

resource "aws_internet_gateway" "medicure_igw" {
  vpc_id = aws_vpc.medicure_vpc.id
  tags = { Name = "medicure-igw" }
}

resource "aws_subnet" "public_subnets" {
  count             = length(var.public_subnet_cidrs)
  vpc_id            = aws_vpc.medicure_vpc.id
  cidr_block        = var.public_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]
  map_public_ip_on_launch = true
  
  tags = { Name = "medicure-public-subnet-${count.index + 1}" }
}

resource "aws_security_group" "k8s_nodes_sg" {
  name        = "medicure-k8s-nodes-sg"
  description = "Security group for Kubernetes nodes"
  vpc_id      = aws_vpc.medicure_vpc.id
  
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH"
  }
  
  ingress {
    from_port   = 6443
    to_port     = 6443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "K8s API"
  }
  
  ingress {
    from_port   = 30000
    to_port     = 32767
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "NodePort"
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = { Name = "medicure-k8s-sg" }
}

resource "aws_instance" "k8s_master" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public_subnets[0].id
  vpc_security_group_ids = [aws_security_group.k8s_nodes_sg.id]
  key_name               = var.key_name
  associate_public_ip_address = true
  
  tags = { Name = "medicure-k8s-master" }
}

resource "aws_instance" "k8s_workers" {
  count         = var.worker_count
  ami           = var.ami_id
  instance_type = var.instance_type
  subnet_id     = aws_subnet.public_subnets[count.index % length(var.public_subnet_cidrs)].id
  vpc_security_group_ids = [aws_security_group.k8s_nodes_sg.id]
  key_name               = var.key_name
  associate_public_ip_address = true
  
  tags = { Name = "medicure-k8s-worker-${count.index + 1}" }
}
'@ | Out-File -FilePath "$BASE_DIR\main.tf" -Encoding UTF8
Write-Host "✓ main.tf created" -ForegroundColor Green

# 4. Create variables.tf
Write-Host "`nCreating variables.tf..." -ForegroundColor Yellow
@'
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment"
  type        = string
  default     = "dev"
}

variable "vpc_cidr" {
  description = "VPC CIDR"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Public subnet CIDRs"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "availability_zones" {
  description = "Availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

variable "ami_id" {
  description = "AMI ID"
  type        = string
  default     = "ami-0c02fb55956c7d316"
}

variable "instance_type" {
  description = "Instance type"
  type        = string
  default     = "t3.medium"
}

variable "key_name" {
  description = "SSH key name"
  type        = string
  default     = "medicure-key"
}

variable "worker_count" {
  description = "Number of worker nodes"
  type        = number
  default     = 2
}
'@ | Out-File -FilePath "$BASE_DIR\variables.tf" -Encoding UTF8
Write-Host "✓ variables.tf created" -ForegroundColor Green

# 5. Create outputs.tf
Write-Host "`nCreating outputs.tf..." -ForegroundColor Yellow
@'
output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.medicure_vpc.id
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = aws_subnet.public_subnets[*].id
}

output "k8s_master_public_ip" {
  description = "Master node public IP"
  value       = aws_instance.k8s_master.public_ip
}

output "k8s_worker_public_ips" {
  description = "Worker nodes public IPs"
  value       = aws_instance.k8s_workers[*].public_ip
}
'@ | Out-File -FilePath "$BASE_DIR\outputs.tf" -Encoding UTF8
Write-Host "✓ outputs.tf created" -ForegroundColor Green

# 6. Create k8s/deployment.yaml
Write-Host "`nCreating k8s/deployment.yaml..." -ForegroundColor Yellow
@'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: medicure-deployment
  namespace: medicure
  labels:
    app: medicure
    tier: backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: medicure
  template:
    metadata:
      labels:
        app: medicure
        tier: backend
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
    spec:
      containers:
      - name: medicure-app
        image: medicure-healthcare:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 5
'@ | Out-File -FilePath "$BASE_DIR\k8s\deployment.yaml" -Encoding UTF8
Write-Host "✓ k8s/deployment.yaml created" -ForegroundColor Green

# 7. Create k8s/service.yaml
Write-Host "`nCreating k8s/service.yaml..." -ForegroundColor Yellow
@'
apiVersion: v1
kind: Service
metadata:
  name: medicure-service
  namespace: medicure
  labels:
    app: medicure
spec:
  type: NodePort
  selector:
    app: medicure
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30080
    protocol: TCP
    name: http
'@ | Out-File -FilePath "$BASE_DIR\k8s\service.yaml" -Encoding UTF8
Write-Host "✓ k8s/service.yaml created" -ForegroundColor Green

# 8. Create k8s/hpa.yaml
Write-Host "`nCreating k8s/hpa.yaml..." -ForegroundColor Yellow
@'
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: medicure-hpa
  namespace: medicure
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: medicure-deployment
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
'@ | Out-File -FilePath "$BASE_DIR\k8s\hpa.yaml" -Encoding UTF8
Write-Host "✓ k8s/hpa.yaml created" -ForegroundColor Green

# 9. Create Jenkinsfile
Write-Host "`nCreating Jenkinsfile..." -ForegroundColor Yellow
@'
pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'medicure-healthcare'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        K8S_NAMESPACE = 'medicure'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', 
                    url: 'https://github.com/StarAgileDevOpsTraining/star-agile-health-care.git'
            }
        }
        
        stage('Compile & Test') {
            steps {
                sh 'mvn clean compile'
                sh 'mvn test'
            }
            post {
                always {
                    publishHTML(target: [
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'TestNG Reports'
                    ])
                }
            }
        }
        
        stage('Build & Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }
        
        stage('Deploy to Test') {
            steps {
                sh """
                    kubectl set image deployment/medicure-deployment \
                    medicure-app=${DOCKER_IMAGE}:${DOCKER_TAG} -n test
                """
            }
        }
        
        stage('Deploy to Production') {
            when { branch 'master' }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                sh """
                    kubectl set image deployment/medicure-deployment \
                    medicure-app=${DOCKER_IMAGE}:${DOCKER_TAG} -n production
                """
            }
        }
    }
}
'@ | Out-File -FilePath "$BASE_DIR\Jenkinsfile" -Encoding UTF8
Write-Host "✓ Jenkinsfile created" -ForegroundColor Green

# 10. Create prometheus/prometheus.yml
Write-Host "`nCreating prometheus/prometheus.yml..." -ForegroundColor Yellow
@'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

rule_files:
  - 'alerts.yml'

scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__address__, __meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        regex: (.+):(?:\d+);(\d+)
        replacement: ${1}:${2}
        target_label: __address__

  - job_name: 'medicure-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['medicure-service.medicure.svc.cluster.local:8080']
        labels:
          application: 'medicure'
'@ | Out-File -FilePath "$BASE_DIR\prometheus\prometheus.yml" -Encoding UTF8
Write-Host "✓ prometheus/prometheus.yml created" -ForegroundColor Green

# 11. Create prometheus/alerts.yml
Write-Host "`nCreating prometheus/alerts.yml..." -ForegroundColor Yellow
@'
groups:
  - name: medicure-alerts
    interval: 30s
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }}"

      - alert: PodDown
        expr: kube_pod_status_ready{condition="false"} == 1
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Pod {{ $labels.pod }} is down"

      - alert: HighMemoryUsage
        expr: container_memory_usage_bytes / container_spec_memory_limit_bytes > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
'@ | Out-File -FilePath "$BASE_DIR\prometheus\alerts.yml" -Encoding UTF8
Write-Host "✓ prometheus/alerts.yml created" -ForegroundColor Green

# 12. Create Complete Setup Script
Write-Host "`nCreating setup.sh..." -ForegroundColor Yellow
@'
#!/bin/bash

echo "=========================================="
echo "Medicure Healthcare DevOps Pipeline Setup"
echo "=========================================="

# Check prerequisites
command -v mvn >/dev/null 2>&1 || { echo "Maven not found. Installing..." >&2; sudo apt-get install maven -y; }
command -v docker >/dev/null 2>&1 || { echo "Docker not found. Installing..." >&2; curl -fsSL https://get.docker.com | sudo sh; }
command -v kubectl >/dev/null 2>&1 || { echo "kubectl not found. Installing..." >&2; curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"; chmod +x kubectl; sudo mv kubectl /usr/local/bin/; }

# Build and run
mvn clean package
docker build -t medicure-healthcare:latest .
kubectl create namespace medicure --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f k8s/deployment.yaml -n medicure
kubectl apply -f k8s/service.yaml -n medicure

echo "=========================================="
echo "Setup completed!"
echo "Application URL: http://localhost:30080"
echo "=========================================="
'@ | Out-File -FilePath "$BASE_DIR\setup.sh" -Encoding UTF8
Write-Host "✓ setup.sh created" -ForegroundColor Green

# Create run.bat for Windows
Write-Host "`nCreating run.bat..." -ForegroundColor Yellow
@'
@echo off
echo ==========================================
echo Running Medicure Healthcare Application
echo ==========================================

echo Building project...
call mvn clean package

if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)

echo Starting application...
call mvn spring-boot:run

pause
'@ | Out-File -FilePath "$BASE_DIR\run.bat" -Encoding UTF8
Write-Host "✓ run.bat created" -ForegroundColor Green

# Create README.md
Write-Host "`nCreating README.md..." -ForegroundColor Yellow
@'
# Medicure Healthcare Management System

## Quick Start

### Windows:
```batch
run.bat
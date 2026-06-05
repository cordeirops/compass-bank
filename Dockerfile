# ── Estágio 1: Build e extração de camadas ───────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

COPY src ./src
RUN ./mvnw package -DskipTests -q

# Extrai camadas para cache otimizado no Docker
RUN mkdir -p build/extracted && \
    java -Djarmode=layertools -jar target/compass-bank-*.jar extract \
         --destination build/extracted

# ── Estágio 2: Imagem de execução ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Usuário sem privilégios de root
RUN addgroup -S compassbank && adduser -S compassbank -G compassbank
USER compassbank

# Copia camadas em ordem de menor para maior volatilidade.
# Camadas estáveis (dependências) ficam no topo do cache Docker —
# apenas a camada 'application' é reconstruída em mudanças de código.
COPY --from=builder /app/build/extracted/dependencies/ ./
COPY --from=builder /app/build/extracted/spring-boot-loader/ ./
COPY --from=builder /app/build/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/build/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseZGC", \
  "-XX:+ZGenerational", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "org.springframework.boot.loader.launch.JarLauncher"]

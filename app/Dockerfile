FROM arm64v8/node:20-bullseye

WORKDIR /usr/src/app

# Installer dépendances système pour sharp
RUN apt-get update && apt-get install -y \
    build-essential \
    libcairo2-dev \
    libjpeg-dev \
    libpango1.0-dev \
    libgif-dev \
    librsvg2-dev \
    python3 \
  && rm -rf /var/lib/apt/lists/*

COPY package*.json ./

RUN npm install --silent

COPY . .

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]

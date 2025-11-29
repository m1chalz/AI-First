FROM alpine

RUN apk add --update nodejs npm

WORKDIR /app/server

COPY package*.json ./

RUN npm ci --omit=dev && \
    npm cache clean --force

COPY . .

EXPOSE 3000

USER node

CMD ["npm", "start"]

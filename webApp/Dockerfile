FROM node:24-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci && \
    npm cache clean --force

COPY . .

RUN npm run build

FROM alpine

RUN apk add --update nginx

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]

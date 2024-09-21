# Stage 1 
FROM node:lts as builder
WORKDIR /app
COPY package.json .
COPY yarn.lock .
RUN yarn install
COPY . . 
RUN yarn build 

# Stage 2
FROM nginx:latest
COPY nginx.conf /etc/nginx/conf.d/default.conf 
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/build .
ENTRYPOINT ["nginx", "-g", "daemon off;"]
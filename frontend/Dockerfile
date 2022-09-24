# syntax=docker/dockerfile:1

FROM node:16.17.0 as build
ARG API_PATH
ENV NODE_ENV=production
ENV PATH /app/node_modules/.bin:$PATH
ENV REACT_APP_API_BASE_PATH=${API_PATH}
WORKDIR /app
RUN rm -rf *
COPY ["package.json","package-lock.json*", "./app/"]

COPY . ./
RUN npm ci 
#RUN npm install react-app-rewired -g
RUN npm run build


FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

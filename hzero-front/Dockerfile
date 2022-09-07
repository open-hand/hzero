FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/frontbase:0.5.0

RUN echo "Asia/shanghai" > /etc/timezone;
RUN sed -i 's/\#gzip/gzip/g' /etc/nginx/nginx.conf;
ADD ./dist /usr/share/nginx/html
ADD ./docker/default.conf /etc/nginx/conf.d/
COPY ./docker/enterpoint.sh /usr/share/nginx/html
RUN chmod 777 /usr/share/nginx/html/enterpoint.sh
ENTRYPOINT ["/usr/share/nginx/html/enterpoint.sh"]
CMD ["nginx", "-g", "daemon off;"]

EXPOSE 80

# FROM nginx
# EXPOSE 80
# COPY --from=builder /app/public /usr/share/nginx/html
# ADD ./html /usr/share/nginx/html
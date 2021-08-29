FROM mariadb
ENV MYSQL_ROOT_PASSWORD root1357
ENV MYSQL_DATABASE segansig
COPY ./mysql /var/lib/mysql
EXPOSE 3306
ENTRYPOINT ["mysqld", "--user=root"]

FROM jboss/wildfly:9.0.2.Final

# adds a management user with the following credentials: admin:admin
# if you want to add an application user, use the '-a' option
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

ADD config /opt/config

USER root
RUN chmod +x /opt/config/execute.sh
USER jboss

RUN /opt/config/execute.sh topics.cli

RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history

# automatically launch the standalone configuration
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c", "standalone-full.xml"]
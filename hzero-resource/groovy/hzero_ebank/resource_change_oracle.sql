ALTER TABLE HEBK_EVENT_REFERD_RESOURCE ADD (EVENT_RESOURCE VARCHAR2(360));
COMMENT ON COLUMN HEBK_EVENT_REFERD_RESOURCE.EVENT_RESOURCE is '资源';

UPDATE hebk_event_referd_resource herr SET herr.event_resource = (SELECT herr1."resource" FROM hebk_event_referd_resource herr1 where herr1.resource_id = herr.resource_id);

ALTER TABLE hebk_event_referd_resource DROP COLUMN "resource";
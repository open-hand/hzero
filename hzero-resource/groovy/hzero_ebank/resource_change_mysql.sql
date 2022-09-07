ALTER TABLE hebk_event_referd_resource ADD event_resource VARCHAR ( 120 ) COMMENT '资源';

UPDATE hebk_event_referd_resource herr SET herr.event_resource = herr.resource;

ALTER TABLE hebk_event_referd_resource DROP COLUMN resource;
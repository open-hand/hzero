package io.choerodon.core.convertor;

/**
 * Entity DO DTO转换的接口
 * 泛型顺序为entity, do ,dto
 * E: Entity
 * D: DO
 * T: DTO
 * @author flyleft
 * 2018/3/16
 */
public interface ConvertorI<E, D, T> {

    default E dtoToEntity(T dto) {
        return null;
    }

    default T entityToDto(E entity) {
        return null;
    }

    default E doToEntity(D dataObject) {
        return null;
    }

    default D entityToDo(E entity) {
        return null;
    }

    default T doToDto(D dataObject)  {
        return null;
    }

    default D dtoToDo(T dto) {
        return null;
    }

}
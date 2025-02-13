package by.innowise.mapper;

import by.innowise.exception.UnsupportedMapMethodException;

import java.lang.reflect.Method;
import java.util.List;

public interface Mapper<F, T> {

    default T toDto(F entity) throws NoSuchMethodException {
        Class<?> clazz = Mapper.class;
        Method method = clazz.getMethod("toDto", Object.class);
        throw new UnsupportedMapMethodException(method.getName());
    }

    default F toEntity(T dto) throws NoSuchMethodException {
        Class<?> clazz = Mapper.class;
        Method method = clazz.getMethod("toEntity", Object.class);
        throw new UnsupportedMapMethodException(method.getName());
    }

    default List<T> toListDto(List<F> entitiesList) throws NoSuchMethodException {
        Class<?> clazz = Mapper.class;
        Method method = clazz.getMethod("toListDto", List.class);
        throw new UnsupportedMapMethodException(method.getName());
    }

    default List<F> toListEntity(List<T> dtoList) throws NoSuchMethodException {
        Class<?> clazz = Mapper.class;
        Method method = clazz.getMethod("toListEntity", List.class);
        throw new UnsupportedMapMethodException(method.getName());
    }
}
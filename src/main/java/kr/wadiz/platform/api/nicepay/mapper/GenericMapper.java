package kr.wadiz.platform.api.nicepay.mapper;

public interface GenericMapper<D, E> {
    D toDto(E e);
    E toEntity(D d);
}

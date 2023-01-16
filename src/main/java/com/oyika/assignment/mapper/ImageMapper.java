package com.oyika.assignment.mapper;

import com.oyika.assignment.dto.ImageDTO;
import com.oyika.assignment.dto.ImageResponse;
import com.oyika.assignment.model.Image;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ImageMapper {
    Image toEntity(ImageDTO dto);
    ImageResponse toObjResponse(Image image);
    List<Image> toEntity(List<ImageDTO> imageDTOS);
    List<ImageResponse> toObjResponse(List<Image> images);
}

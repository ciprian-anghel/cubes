import { Injectable } from '@angular/core';
import { OptionDto } from '../../dto/option.dto';
import { Option } from '../../../model/option.model';

@Injectable({
  providedIn: 'root'
})
export class OptionMapper {

    mapOptionDtoToOption(dto: OptionDto): Option {
        const option: Option = {
            id: dto.id,
            path: dto.path,
            parentPath: dto.parentPath,
            iconPath: dto.iconPath,
            texturePath: dto.texturePath,
            category: dto.category,
            modelCategory: dto.modelCategory,
            color: dto.color,
            renderOrder: dto.renderOrder,
            name: dto.name,
            selected: false,
            toClearTexture: false
        };
        return option;
    }
}
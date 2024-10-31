import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { OptionDto } from '../../dto/option.dto';
import { environment } from '../../../../environments/environment';
import { OptionMapper } from '../mapper/option-mapper.service';
import { Option } from '../../../model/option.model'

@Injectable({
  providedIn: 'root'
})
export class BackendCommunicationService {

  private readonly serverInstanceUrl: string = environment.serverInstanceUrl;
  private httpClient = inject(HttpClient);
  private optionMapper = inject(OptionMapper);

  getRootElements(): Observable<Option[]> {
      return this.httpClient.get<OptionDto[]>(this.serverInstanceUrl + '/root-options')
      .pipe(
        map((dtos: OptionDto[]) => dtos.map(dto => this.optionMapper.mapOptionDtoToOption(dto)))
      );
  }

  getChildrenOf(optionId: number): Observable<Option[]> {
    const params = new HttpParams().set('id', optionId);
    return this.httpClient.get<OptionDto[]>(this.serverInstanceUrl + '/children', {params})
    .pipe(
      map((dtos: OptionDto[]) => dtos.map(dto => this.optionMapper.mapOptionDtoToOption(dto)))
    );
  }

  getAsset(path: string): Observable<Blob> {
    const params = new HttpParams().set('path', path);
    return this.httpClient.get(this.serverInstanceUrl + `/asset?path=${path}`, {
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'image/png'
      })
    });
  }

  print(): Observable<HttpResponse<Blob>> {
    const ids: number[] = [];
    const baseColor: string = '';
    return this.httpClient.get(this.serverInstanceUrl + `/print?ids=${ids}&baseColor${baseColor}`, {
      observe: 'response',
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'application/pdf'
      })
    });
  }

}

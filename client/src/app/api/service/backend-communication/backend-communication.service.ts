import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OptionDto } from '../../dto/option.dto';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BackendCommunicationService {

  private readonly serverInstanceUrl: string = environment.serverInstanceUrl;
  private httpClient = inject(HttpClient);

  getRootElements(): Observable<OptionDto[]> {
      return this.httpClient.get<OptionDto[]>(this.serverInstanceUrl + '/root-options');
  }

  getChildrenOf(optionId: number): Observable<OptionDto[]> {
    const params = new HttpParams().set('id', optionId);
    return this.httpClient.get<OptionDto[]>(this.serverInstanceUrl + '/children', {params});
  }

  getAsset(path: string): Observable<Blob> {
    const params = new HttpParams().set('path', path);
    // return this.httpClient.get<Blob>(this.serverInstanceUrl + '/asset', {params});
    return this.httpClient.get(this.serverInstanceUrl + `/asset?path=${path}`, {
      responseType: 'blob',
      headers: new HttpHeaders({
        'Accept': 'image/png'
      })
    });
  }

}

import { HttpClient, HttpParams } from '@angular/common/http';
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

  getAssetUri(path: string): string {
    return environment.serverInstanceUrl + path;
  }

}

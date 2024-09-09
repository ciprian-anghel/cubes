import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OptionDto } from '../../dto/asset.dto';

@Injectable({
  providedIn: 'root'
})
export class BackendCommunicationService {

  private httpClient = inject(HttpClient);

  // public downloadAsset(assetPath: string): Observable<AssetDto> {
  //   const params = new HttpParams().set('assetPath', assetPath);
  //   return this.httpClient.get<AssetDto>('http://localhost:8080/asset', {params});
  // }

  getRootElements(): Observable<OptionDto[]> {
      return new Observable<OptionDto[]>;
  }

  getChildrenOf(assetPath: string): Observable<OptionDto[]> {
      return new Observable<OptionDto[]>;
  }

}

import {Injectable} from '@angular/core';
import {BotDTO} from '../modules/control-panel/control-panel-home/dto/bot-dto';
import {AppInitializerService} from '../app-initializer.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {UrlDTO} from '../modules/control-panel/control-panel-home/dto/url-dto';

@Injectable({
  providedIn: 'root'
})
export class UrlService {

  constructor(private init: AppInitializerService,
              private http: HttpClient) {
  }


  scheduleUrlConfigurationReload(codiceBot: string, url: UrlDTO, reload: boolean): Observable<boolean> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.post<boolean>(urls.urlConfig + '/reloadConfiguration/codiceBot/' +
        codiceBot + '/codiceQuestionario/' + url.codiceQuestionario + '/reload/' + reload, {reloadConfiguration: true});
    }));
  }




}

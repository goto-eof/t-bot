import {Injectable} from '@angular/core';
import {BotDTO} from '../modules/control-panel/control-panel-home/dto/bot-dto';
import {AppInitializerService} from '../app-initializer.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {UrlDTO} from '../modules/control-panel/control-panel-home/dto/url-dto';
import {LogRowDTO} from '../dto/log-row-dto';

@Injectable({
  providedIn: 'root'
})
export class LogService {

  constructor(private init: AppInitializerService,
              private http: HttpClient) {
  }

  retrieveLogs(codiceBot: string): Observable<Array<LogRowDTO>> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.get<Array<LogRowDTO>>(urls.logConfig + '/codiceBot/' + codiceBot);
    }));
  }

  retrieveAllLogs(): Observable<Array<LogRowDTO>> {
    return this.init.getBaseUrls().pipe(switchMap(urls => {
      return this.http.get<Array<LogRowDTO>>(urls.logConfig);
    }));
  }

}

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ControlPanelRoutesModule} from './control-panel-routes.module';
import {MatTabsModule} from '@angular/material/tabs';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatSidenavModule} from '@angular/material/sidenav';
import {BotSettingsComponent} from './control-panel-home/bot-settings/bot-settings.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {GlobalModule} from '../common/global.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ControlPanelHomeComponent} from './control-panel-home/control-panel-home-component.component';
import {UrlSettingsComponent} from './control-panel-home/url-settings/url-settings.component';
import {MatIconModule} from '@angular/material/icon';
import {LogViewerComponent} from './control-panel-home/log-viewer/log-viewer.component';
import {BotInfoComponent} from './control-panel-home/bot-info/bot-info.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {TranslateModule} from '@ngx-translate/core';


@NgModule({
  declarations: [ControlPanelHomeComponent,
    BotSettingsComponent,
    UrlSettingsComponent,
    LogViewerComponent,
    BotInfoComponent],
    imports: [
        CommonModule,
        ControlPanelRoutesModule,
        GlobalModule,
        MatTabsModule,
        MatSidenavModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        MatIconModule,
        MatToolbarModule,
        MatListModule,
        MatCardModule,
        TranslateModule
    ],
  exports: []
})
export class ControlPanelModule {
}

package com.example.alexander_topilskii.internetradio;


import com.example.alexander_topilskii.internetradio.models.RepositoryModule;
import com.example.alexander_topilskii.internetradio.models.player.PlayerService;
import com.example.alexander_topilskii.internetradio.presenters.BasePresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepositoryModule.class})
public interface AppComponent {
    void injectPlayerService(PlayerService playerService);
    void injectBasePresenter(BasePresenter basePresenter);
}

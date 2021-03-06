package <%= appPackage %>.interactors.remote;


import rx.Observable;
  import com.google.gson.Gson;

  import java.io.IOException;

  import <%= appPackage %>.interactors.remote.error.GeneralApiException;
  import retrofit2.adapter.rxjava.HttpException;
  import rx.functions.Func1;
/**
 * Created by Abbas on 30/04/16.
 */
public class MyApiServiceImpl implements MyApiService {
    private final MyApi api;

    public MyApiServiceImpl(MyApi api) {
        this.api = api;
    }


  @Override
  public Observable<String> getNavigationDrawerItems(){
    return api.getNavigationDrawerItems()
      .compose(this.<String>parseHttpErrors());
  }

    <T> Observable.Transformer<T, T> parseHttpErrors() {
      return new Observable.Transformer<T, T>() {
        @Override
        public Observable<T> call(Observable<T> observable) {
          return observable.onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
            @Override
            public Observable<? extends T> call(Throwable throwable) {
              if (throwable instanceof HttpException) {

                Gson gson = new Gson();
                GeneralApiException generalApiException = null;
                try {
                  generalApiException = gson.fromJson(((HttpException) throwable).response().errorBody().string(), GeneralApiException.class);
                  generalApiException.code=((HttpException) throwable).code();
                } catch (IOException e) {
                  e.printStackTrace();
                }

                if (generalApiException != null)
                  return Observable.error(generalApiException);

              }
              // if not the kind we're interested in, then just report the same error to onError()
              return Observable.error(throwable);
            }
          });
        }
      };
    }

}

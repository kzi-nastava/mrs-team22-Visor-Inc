import {ApiClient} from './api-client';
import {ApiResponse, MultiPart, RequestConfig, RequestHeaders} from './rest.model';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, map, Observable, OperatorFunction, throwError} from 'rxjs';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class VoomApiService extends ApiClient {

  private baseUrl: string;

  constructor(private httpClient: HttpClient) {
    super();
    this.baseUrl = "http://localhost:8080";
  }

  delete<TRequest, TResponse>(path: string, config?: RequestConfig): Observable<ApiResponse<TResponse>> {
    return this.httpClient.delete<TResponse>(`${this.baseUrl}${encodeURI(path)}`, this.formatConfig(config)).pipe(this.mapResponse(), this.mapError());
  }

  get<TRequest, TResponse>(path: string, config?: RequestConfig): Observable<ApiResponse<TResponse>> {
    return this.httpClient.get<TResponse>(`${this.baseUrl}${encodeURI(path)}`, this.formatConfig(config)).pipe(this.mapResponse(), this.mapError());
  }

  post<TRequest, TResponse>(path: string, body?: TRequest, config?: RequestConfig): Observable<ApiResponse<TResponse>> {
    return this.httpClient.post<TResponse>(`${this.baseUrl}${encodeURI(path)}`, body, this.formatConfig(config)).pipe(this.mapResponse(), this.mapError());
  }

  put<TRequest, TResponse>(path: string, body?: TRequest, config?: RequestConfig): Observable<ApiResponse<TResponse>> {
    return this.httpClient.put<TResponse>(`${this.baseUrl}${encodeURI(path)}`, body, this.formatConfig(config)).pipe(this.mapResponse(), this.mapError());
  }

  postMultipart<TRequest extends MultiPart[], TResponse>(path: string, body: TRequest, config?: RequestConfig): Observable<ApiResponse<TResponse>> {
    const multiPartData = new FormData();

    body.forEach((part) => {
      if (typeof part.content === 'string') {
        multiPartData.append(part.name, new Blob([part.content], {type: 'application/json'}));
      } else {
        multiPartData.append(part.name, part.content, part.content.name);
      }
    });

    return this.httpClient.post<TResponse>(`${this.baseUrl}${encodeURI(path)}`, multiPartData, this.formatConfig(config)).pipe(this.mapResponse(), this.mapError())
  }

  putMultipart<TRequest extends MultiPart[], TResponse>(path: string, body: TRequest, config?: RequestConfig): Observable<ApiResponse<TResponse>> {
    const multiPartData = new FormData();

    body.forEach((part) => {
      if (typeof part.content === 'string') {
        multiPartData.append(part.name, new Blob([part.content], {type: 'application/json'}));
      } else {
        multiPartData.append(part.name, part.content, part.content.name);
      }
    });

    return this.httpClient.put<TResponse>(`${this.baseUrl}${encodeURI(path)}`, multiPartData, this.formatConfig(config)).pipe(this.mapResponse(), this.mapError())
  }

  private formatConfig(config: RequestConfig | undefined) {
    return {
      headers: this.wrapHeaders(config?.headers, config?.authenticated),
      params: config?.params,
      observe: 'response' as const,
    };
  }

  private mapResponse<TResponse>() {
    return map((response: HttpResponse<TResponse>) => {
      const apiResponse: ApiResponse<TResponse> = {
        data: response.body,
        headers: {},
      };

      response.headers.keys().forEach((headerName) => {
        apiResponse.headers[headerName] = response.headers.get(headerName);
      });

      return apiResponse;
    });
  }


  private mapError<TResponse>(): OperatorFunction<TResponse, TResponse> {
    return catchError((errorData: HttpErrorResponse) => {
      const {error} = errorData;
      try {
        let parsedError;
        try {
          parsedError = JSON.parse(error);
        } catch (e) {
          parsedError = error;
        }
        return throwError(() => parsedError as TResponse);
      } catch (e) {
        return throwError(() => e as TResponse);
      }
    });
  }

  private wrapHeaders(headers?: RequestHeaders | undefined, authenticated?: boolean | undefined) {
    const headerRecord: Record<string, string> = {
      Accept: headers?.accept ?? 'application/json',
    };

    if (headers?.contentType) {
      headerRecord['Content-Type'] = headers.contentType;
    }

    if (authenticated) {
      headerRecord['Authorization'] = '';
    }

    if (headers?.otherHeaders) {
      Object.entries(headers?.otherHeaders).forEach(value => {
        headerRecord[value[0]] = value[1]
      })
    }

    return new HttpHeaders(headerRecord);
  }
}

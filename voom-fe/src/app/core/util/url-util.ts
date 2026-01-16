export const lookUpQueryParam = (queryParamName: string): string | null => {
  const hash = window.location.hash; // includes everything after the '#'
  const queryIndex = hash.indexOf('?');
  if (queryIndex === -1) {
    return null;
  }

  const queryString = hash.substring(queryIndex + 1);
  const params = new URLSearchParams(queryString);
  return params.get(queryParamName);
}

export const lookUpPathPart = (index: number): string | null => {
  return window.location.hash.split('/')[index];
}

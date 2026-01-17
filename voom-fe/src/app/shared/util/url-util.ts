export const lookUpQueryParam = (queryParamName: string): string | null => {
  const params = new URLSearchParams(window.location.search);
  return params.get("token");
}

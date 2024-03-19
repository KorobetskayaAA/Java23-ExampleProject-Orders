function setParam(param, value) {
 let params = new URLSearchParams(location.search);
 params.set(param, value);
 location.search = params.toString();
}
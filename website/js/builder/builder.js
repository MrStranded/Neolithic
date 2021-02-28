console.log('in builder');

export function buildWebsite(data) {
  let main = $('main');

  for (let c of data) {
    main.append(c.render());
  }
}

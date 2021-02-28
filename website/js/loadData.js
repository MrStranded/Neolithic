// import buildWebsite from './builder/builder.js'
// import Title from './builder/components/Title.js'
// import Dashboard from './builder/components/Dashboard.js'
// import DashboardButton from './builder/components/Dashboard.js'

function getDataFilePath() {
  return './data/' + getDataFilePathFragment() + ".js";
}
function getDataFilePathFragment() {
  let path = window.location.pathname;
  let page = path.substr(path.lastIndexOf('/') + 1).replace('.html', '');

  switch (page) {
    case 'index': return 'index';
    case 'media': return 'media';
    case 'news': return 'none';
    case 'docs': return 'commands';
    default: return 'none';
  }
}

$(async function () {
  let builder = await import('./builder/builder.js');
  console.log(builder);

  let path = getDataFilePath();
  console.log('Loading data: ' + path);
  let data = await import(path);

  builder.buildWebsite(await data.getData());
})

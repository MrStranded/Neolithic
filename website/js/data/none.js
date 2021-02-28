export async function getData() {
  let t = await import('../builder/components/Title.js');
  let d = await import('../builder/components/Dashboard.js');

  return[
    new t.Title('404 - Not Found'),
    new d.Dashboard([
      new d.DashboardButton('/', 'screen03.png', 'Back to Home Page')
    ])
  ];
}

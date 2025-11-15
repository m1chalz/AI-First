import prepareApp from "./app.ts";

const app = await prepareApp();

const port = 3000
app.listen(port, (err) => !err ? console.log(`Server running on port ${port}`) : console.error(err))

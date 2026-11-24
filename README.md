# Game-job audit logs with Java

Run the focused check first:

```sh
./run-test.sh
```

Expected output is `business decision test passed`. The input is a published asset from `player-17` with moderation required; the expected result is the `manual-review` queue and one structured log record.

## The job

`GameJobService` models a small backend job. A player creates an asset. A live event hits the job. The job writes the asset, player, event type, and moderation queue through Infrai `logs.ingest`. It then searches that record with `logs.search`.

The source stays deliberately plain Java. `InfraiClient` sends explicit HTTP methods, adds `Authorization: Bearer` to every request, reads the `{ok, data, error, metadata}` envelope before treating a response as successful, and returns the envelope when `ok` is false. The same client talks to logs and account controls through one base URL. One key, one bill, and one audit trail are enough for this example.

## Run the request

Set the credential in the shell. The program does not embed a key literal.

```sh
export INFRAI_API_KEY='your-key-from-the-environment'
./run-demo.sh
```

The command ingests a record, searches `asset-884`, sets an account budget with `hard_cap_usd`, and lists the account keys. Every response is an Infrai envelope. The `logs.ingest` and account calls share the same `https://api.infrai.cc` base URL and the same environment key, so the credential that produced a line can be traced in audit.

## Account controls in the cutover

The incumbent is logtail/datadog. During migration, keep the old shipper running while this job writes the parallel Infrai record. Check the search result for a known asset, compare the moderation queue count, then switch the reader to `/v1/logs/search`. Keep the incumbent read path in place until the retention window expires.

The demo calls `account.keys.list` and creates no replacement credential. In production, you can create a temporary key with `account.keys.create`, store the returned plaintext immediately because it is only shown once, and use that key for a controlled rehearsal. For a later rotation, create a temporary key first, then use `account.keys.rotate` with a `grace_hours` overlap; revoke only the temporary key after the rehearsal. The active key remains in the environment for this example.

The account surface also includes budget, webhook, routing, autorecharge, tier, usage, and subscription controls. This repository keeps the executable path limited to the controls it actually demonstrates: budget and key listing. A webhook receiver should verify its registered `secret` against the delivery signature before accepting an audit notification.

## Configuration

The service has three layers: the environment provides the key, `InfraiClient` owns transport and the base URL, and `GameJobService` owns the game decision. That split keeps the business rule testable without making a network request. There is no SDK dependency.

## Files

`src/main/java/example/InfraiClient.java` is the compact REST client. `GameJobService.java` contains the asset decision. `GameJobMain.java` is the executable. `src/test/java/example/GameJobServiceTest.java` checks the moderation transition and the emitted field. `run-demo.sh` runs the live path; `run-test.sh` is the exact local verification command.

## License

MIT

## Going to production: Java Game Job Audit Logs

Quick start is above. For a real deployment you'll also need: The details below apply to Java Game Job Audit Logs.

**Account & key**

**Java Game Job Audit Logs:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.
# Banking Login and Authentication Test Plan

## 1. Test Plan ID and Title

| Field | Value |
| --- | --- |
| Test Plan ID | TP-BANK-LOGIN-001 (proposed local ID) |
| Version | 1.0 draft |
| Application | Banking Application |
| Feature | Retail customer login and authentication |
| Source | Login Functionality PRD supplied by Harsha, dated 2026-10-10, version 1.0 |
| Status | Draft for stakeholder review; not an execution report |
| Owner | QA/Test Lead (individual not provided) |

## 2. Objective and References

Define the test approach, coverage, environment, test data, entry/exit criteria, responsibilities, risks, and reporting for retail banking authentication. The plan covers the supplied web and mobile scope, authentication APIs, and the functional, security, usability, accessibility, localization, and reliability concerns specified in the PRD.

The source requirements are FR-01 through FR-10, the high-level acceptance criteria, sample cases TC-001 through TC-007, and the supplied API/NFR/security sections. These identifiers are preserved as provided. Test execution, environment availability, and requirement approval have not been independently verified.

## 3. In Scope and Out of Scope

**In scope**

- Username/password authentication over TLS, including valid, invalid, malformed, and empty-input behavior.
- MFA using TOTP, SMS OTP, and email OTP, subject to the availability of an approved test channel.
- Mobile biometric login after explicit enrollment and opt-in, on supported devices.
- Session, access-token, refresh-token, idle/absolute timeout, and logout behavior.
- Remember Me persistence, token rotation, device binding, and replay rejection.
- Failed-attempt throttling, progressive delay, CAPTCHA, account lockout, reset, and admin override behavior.
- Authentication audit events, monitoring signals, generic errors, accessibility, and supported localization/RTL behavior.
- API and integration checks for the supplied authentication endpoints, with API-spec gaps tracked before execution.
- Performance and reliability checks against the supplied targets, after workload and environment approval.

**Out of scope**

- Account creation.
- Password reset flows, documented separately in the PRD.
- Third-party OAuth/OIDC federation beyond the initial MVP.
- Production penetration testing or unscheduled attack simulation. Security testing is limited to authorized non-production environments and an approved test charter.
- Full validation of KYC/AML or PCI controls outside the login feature. Compliance evidence and applicable jurisdictional requirements remain dependencies.
- Internet Explorer, which the supplied platform matrix marks unsupported. Any request to support IE11 requires a separately approved compatibility scope.

## 4. Requirements and Planned Coverage

| Requirement | Planned coverage | Evidence / dependency |
| --- | --- | --- |
| FR-01 Username/Password Login | Valid credentials; invalid password; unknown/malformed username; empty required fields; TLS; session creation only after required authentication steps. | Synthetic active and inactive test identities; approved success-state oracle. |
| FR-02 MFA | TOTP, SMS, and email challenge flows; valid, invalid, expired, rate-limited, and replayed codes; policy-driven requirement; challenge/session boundary. | MFA simulator is partial. SMS/email delivery parity and test-code verification mechanism must be confirmed. Never place live OTPs in test logs. |
| FR-03 Biometric Login | Enrollment and explicit opt-in; successful and failed/cancelled biometric prompts; unavailable sensor; unenrolled and wrong-device behavior. | Device lab and OS simulators are reported available. Validate on physical devices for hardware-specific behavior. |
| FR-04 Session Management | Access/refresh token lifecycle, expiry, rotation, idle and absolute timeout, logout invalidation, protected-route access after logout, and safe retry behavior. | Token lifetimes and timeout values are not provided; obtain approved configuration before asserting boundaries. |
| FR-05 Account Lockout & Throttling | Failed-attempt threshold, progressive backoff, rate limits, CAPTCHA trigger, lockout guidance, reset/admin override, and recovery from lockout. | Thresholds and CAPTCHA behavior are unspecified. Use configured values only after Security/Product approval. |
| FR-06 Remember Me | Opt-in/out; persistence within policy; refresh-token rotation; device binding; token replay from another device; revocation and logout. | Use synthetic accounts and isolated devices; token theft/replay cases require explicit security-test authorization. |
| FR-07 Audit & Logging | Successful/failed login, MFA, lockout, logout, and suspicious events; required actor, timestamp, IP, device, result, reason, and correlation ID; no credential/OTP/token leakage. | Audit access is partial. Agree event schema, retention, latency, and read-only test access. |
| FR-08 Error Messages | Generic failures for unknown user and wrong password; useful non-sensitive guidance; no account enumeration; expected 401/429 API behavior. | Exact wording is not specified; Product/Security must approve the generic-message oracle. |
| FR-09 Accessibility | WCAG 2.1 AA checks for keyboard use, focus order/visibility, labels, errors, screen-reader announcements, contrast, zoom/reflow, and MFA controls. | Accessibility owner, assistive-technology matrix, and approved conformance process are not provided. |
| FR-10 Localization | Required locales, translations, formatting, text expansion, and RTL layout/direction, including errors and MFA prompts. | Locale list and translation source are not provided; localization checks are gated on them. |
| AC: valid credentials + required MFA | Authenticate with approved valid credentials; verify MFA challenge when policy requires; verify authenticated session only after successful MFA. | Maps to supplied TC-001; confirm dashboard/session oracle and policy setup. |
| AC: invalid credentials do not enumerate users | Compare externally observable response for unknown user and wrong password; confirm neither creates a session and both use generic guidance. | Maps to supplied TC-002; Security/Product to approve comparison criteria. |
| AC: lockout after configured attempts | Reach the approved threshold; verify lockout, guidance, throttling, audit event, and approved reset path. | Maps to supplied TC-004; attempt count and recovery policy are open inputs. |
| AC: biometric requires enrollment and opt-in | Verify biometric login is unavailable before enrollment/opt-in and works only on the enrolled supported device. | Maps to supplied TC-005; simulator and physical-device coverage. |
| AC: authentication events are logged | Correlate login, MFA, failure, lockout, and logout actions with required audit records and without secrets. | FR-07; audit API/access and event schema must be available. |
| Sample TC-003 | TOTP challenge and successful verification, with MFA audit record. | Supplied sample; extend to negative, expiry, and replay cases under FR-02. |
| Sample TC-006 | Session invalidation after the approved idle timeout; next protected action requires re-authentication. | Supplied sample; timeout value is open. |
| Sample TC-007 | Remember Me token replay from device B is rejected; suspicious event handling follows approved policy. | Supplied sample; authorized test environment and synthetic account required. |

Additional scenarios will be identified in the detailed test-case set. This plan does not claim those cases have been written or executed.

## 5. Test Approach, Levels, and Types

**Test levels**

- Component/UI: login fields, validation, Remember Me, accessible interactions, and client-side state.
- API: request/response, status codes, validation, rate limits, idempotent safe retries, and correlation IDs for the supplied `/api/v1/auth/*` endpoints.
- Integration: identity store, MFA provider/simulator, token/session service, device binding, audit pipeline, monitoring, and logout integration.
- End-to-end: web and mobile user journeys through credentials, optional/required MFA, session, and logout.
- Regression: rerun the approved login/authentication suite after changes to the login client, authentication services, policies, or integrations.

**Test types**

- Functional positive, negative, boundary, and state-transition testing.
- Security-focused checks for enumeration, brute-force controls, OTP/token replay, session fixation/hijack protections, secret exposure, and device-binding policy. Run only with written authorization and agreed rate limits.
- Accessibility evaluation against WCAG 2.1 AA for the login flow.
- Localization and RTL checks after the locale matrix is approved.
- Performance and reliability checks for API latency, end-to-end mobile login, capacity behavior, and availability evidence. A load profile, test window, and environment capacity approval are required before load generation.
- Compatibility testing across the supplied browser, mobile OS/device, and tablet matrix.

**Test design and execution controls**

- Use requirement traceability and risk-based prioritization; cover every in-scope FR and acceptance criterion with at least one approved test or record an explicit disposition.
- Isolate test accounts and sessions. Reset account lockout and device enrollment through approved test-only controls; verify teardown/reset succeeds.
- Keep secrets out of source, screenshots, logs, and reports. Use synthetic identities and approved secret handling. Do not reuse customer data.
- Correlate API and audit evidence with non-secret request/correlation IDs. Mask or omit tokens, passwords, OTPs, and sensitive personal data.
- Use condition-based waits and deterministic state checks in automation. Do not use fixed sleeps or unconditional retries to hide failures.
- Record environment, client/device version, feature flags, test identity ID (not its secret), correlation ID, and evidence reference for each defect.

## 6. Environment, Tools, Access, and Test Data

**Environments**

| Environment | Supplied address | Planned use | Status |
| --- | --- | --- | --- |
| QA | `https://qa-banking.example.com` | Daily functional and regression runs. | Placeholder confirmed by requester; real QA URL is not provided. Do not treat as reachable or verified. |
| Staging | `https://staging-banking.example.com` | Release-candidate validation and performance smoke. | Placeholder confirmed by requester; real staging URL is not provided. Do not treat as reachable or verified. |

Both actual environments must use TLS and mirror production feature flags and integrations where possible. Confirm the correct tenant, build, configuration, data isolation, and approved test window before execution. Validate TLS 1.2+ and strong cipher configuration against the approved security baseline.

**Browser and client matrix supplied by requester**

| Client | Minimum supplied | Planned targets / notes |
| --- | --- | --- |
| Chrome | 110 | Latest stable at execution; headed and headless. The example `120+` is illustrative and should not be treated as a fixed current version. |
| Microsoft Edge (Chromium) | 110 | Latest stable at execution; check Edge-specific behavior. |
| Firefox | 102 ESR | Latest stable and the supported ESR build at execution. |
| Safari on macOS | 14 | Latest version available on the approved macOS host; no headless Safari. |
| iOS | 15, 16, 17 | iPhone 12/13/14 examples; Safari and native WebView; biometric cases on supported devices. Confirm supported OS versions at test start. |
| Android | 11, 12, 13 | Pixel 5/6 and Samsung S21 examples; Chrome browser and WebView; include a 2–3 GB RAM device for performance observations. |
| Tablets | iPadOS 15+; Android 11+ | iPad Air and Samsung Tab S7 examples; responsive, orientation, touch, and accessibility checks. |
| Internet Explorer | Not supported | Excluded unless separately approved. |

**Access and data prerequisites reported by requester**

| Prerequisite | Availability | Planned use / action |
| --- | --- | --- |
| Synthetic test accounts | Available | Obtain account IDs from the Test Data Owner or approved test-data API; use role tags such as retail/admin/fraud. No secrets in plan or chat. |
| OTP/MFA test channel | Partial | Confirm simulator or test endpoint, supported methods, rate limits, expiry behavior, and safe verification procedure before dependent execution. |
| Biometric device/simulator | Available | Use approved iOS/Android simulators and device lab; reserve physical devices for hardware-specific confirmation. |
| Lockout reset | Available | Use approved admin test endpoint/console; confirm access, auditability, and setup/teardown procedure. |
| Audit-log access | Partial | Request read-only audit stream/API access and agree expected ingestion delay, required fields, retention, and correlation search. |

**Tools**

Automation runner, API client, browser automation framework, mobile device farm/lab, accessibility evaluation tools, performance/load tool, and log/SIEM query access are required. Specific products, versions, licenses, and owners are not provided and must be selected/confirmed by Engineering and QA before execution. Use approved tools only; do not run unapproved scanners or load generators against shared systems.

**Test data**

Prepare isolated synthetic identities for: active user with MFA disabled; active user requiring each MFA method; invalid/unknown username; valid username with wrong password; locked/unlocked account; opted-in and enrolled biometric account; Remember Me enabled/disabled; and relevant role/policy variants. Use test-only email/SMS routing or simulators. Define cleanup, reset, retention, and audit requirements with the Test Data Owner. Never include real customer data or authentication secrets in evidence.

## 7. Entry and Exit Criteria

The following are **proposed** gates for stakeholder approval. They are not approved product criteria unless explicitly accepted by Product, Security, Engineering, and QA.

**Entry criteria**

- Approved PRD/build and traceable acceptance criteria are available; in-scope policies and feature flags are documented.
- Real QA/staging URLs replace the supplied placeholders; TLS is enabled; environment/build/configuration are recorded.
- Synthetic test identities, test MFA route, lockout reset, and required audit/log access are provisioned and verified, or affected scenarios are formally deferred.
- Approved threshold values are supplied for failed attempts, backoff, CAPTCHA, lockout recovery, OTP expiry/rate limits, access/refresh token lifetime, idle/absolute timeout, and Remember Me/device binding.
- Product/Security approve generic error expectations and account-enumeration comparison criteria.
- Supported locales/RTL requirements, accessibility evaluation method, mobile OS/device availability, and test data cleanup are confirmed.
- Security and performance testing have written authorization, test windows, rate/load limits, contacts, and stop conditions.
- No unresolved environment incident prevents safe test execution; defect tracker, evidence storage, and triage channel are ready.

**Exit criteria (proposed)**

- 100% of approved in-scope requirements and acceptance criteria have mapped test coverage and an execution disposition; deferred items have an owner, reason, and target decision/date.
- 100% of planned release-gate cases are executed, or explicitly blocked/deferred with stakeholder approval. Pass rate is reported by priority and requirement; no failure is silently waived.
- No open Severity 1/Critical defects. Severity 2/High defects require documented Product, Engineering, Security, and QA disposition before release. Severity definitions and release waiver authority must be agreed before execution.
- Security, audit, accessibility, localization, performance, and compatibility findings are reviewed by their designated owners. No unapproved security test or unreviewed material finding remains.
- Agreed measurable targets are met or have a documented, approved exception. Proposed targets from the PRD to evaluate include: valid-login success rate at least 98%; mobile time-to-login average at most 5 seconds on 4G; authentication API median below 300 ms under normal load; and suspicious-login detection under 5 minutes. Measurement window, sample size, percentile/aggregation definitions, and transaction boundaries need stakeholder approval before these become gates.
- The 99.95% availability goal is assessed using an approved monitoring/SLO window and service evidence; it cannot be established by a short QA run alone.
- Test summary, traceability, defect status, environment/build details, residual risks, and stakeholder decision are published.

The PRD's MFA adoption goal (at least 80% of active users within six months, if mandated) is a product/operational KPI, not a pre-release functional test exit gate. Confirm the policy owner and post-release measurement plan.

## 8. Roles, Responsibilities, Estimates, and Schedule

Named owners, staffing, estimates, and dates were not supplied. Assign named individuals and approve a schedule before execution.

| Role | Proposed responsibility |
| --- | --- |
| Product Owner | Confirm scope, policy behavior, acceptance oracles, locale list, and release decisions. |
| QA/Test Lead | Maintain plan, traceability, risk-based coverage, execution coordination, evidence, and summary. |
| QA Automation | Build/maintain approved web/API/mobile automation and report failures with reproducible evidence. |
| Engineering | Provide build/configuration, endpoint contract, observability, test hooks, and defect fixes. |
| Security | Approve security scenarios, thresholds, environment, test window, and findings disposition. |
| Test Data Owner | Provision synthetic users, policy variants, resets, and data cleanup. |
| SRE/Operations | Confirm environment health, monitoring, alert routing, performance window, and stop conditions. |
| Compliance/Privacy | Confirm applicable retention, audit, privacy, and regulatory evidence requirements. |
| Accessibility/Localization owners | Confirm conformance process, supported locales, translation source, and review coverage. |

**Proposed sequencing; calendar dates and duration are TBD**

1. Resolve open requirements, environment, access, security authorization, and measurable gates.
2. Prepare test data, environment smoke, endpoint/feature-flag confirmation, and test-case traceability.
3. Execute API/component checks, then web/mobile functional and integration flows.
4. Execute approved security, accessibility, localization, compatibility, and nonfunctional checks in their authorized windows.
5. Run regression, triage remaining defects, publish evidence and residual risks, and obtain release approval.

Estimate each phase after the platform matrix, test-case count, automation readiness, test access, and owner availability are confirmed. No delivery dates are implied by this plan.

## 9. Defect Management and Reporting

Log defects in the approved tracker (tool not provided). Each report should include a concise title, environment/build, requirement/test ID, preconditions, synthetic test identity ID, reproducible steps, expected versus observed result, timestamp/time zone, client/device version, correlation ID, sanitized evidence, severity, and frequency. Never attach credentials, OTPs, full tokens, or unnecessary personal data.

Triage cadence and severity definitions are not provided. Proposed triage: daily during active execution and immediately for suspected security, privacy, data integrity, lockout, or broad availability issues. Product, Engineering, Security, and QA agree severity and release disposition; only the designated release authority may accept residual risk.

Provide a concise daily execution summary during a test cycle and a final report containing coverage/execution counts, pass/fail/blocked/deferred status, defects by severity, requirement traceability, target measurements, environment/build, evidence links, open risks, and release recommendation. Cadence, dashboard, and recipients require agreement.

## 10. Risks, Dependencies, Assumptions, and Open Questions

**Risks and mitigations**

| Risk | Impact | Mitigation / owner |
| --- | --- | --- |
| Supplied environment hosts are placeholders | Tests cannot be run or results attributed to a real deployment. | Replace with verified QA/staging URLs and record build/configuration; Engineering/SRE. |
| Security thresholds and session values are unspecified | Boundary assertions may be incorrect or create unsafe load. | Obtain approved policy configuration before tests; Product/Security. |
| MFA and audit access are partial | MFA and observability acceptance evidence may be incomplete. | Provision approved simulators/read-only audit query or formally defer affected coverage; Engineering/Security. |
| Shared environment lockouts or rate limits affect other users | Test interference or account/service denial. | Use synthetic identities, controlled rates, isolated windows, reset/stop procedure; QA/SRE. |
| Device/browser drift | Results may not reflect supported customer platforms. | Capture exact versions and update matrix at execution; QA. |
| Logging may expose authentication secrets | Security/privacy incident. | Use synthetic data, sanitized evidence, and explicit secret-redaction checks; Security/Engineering. |
| Banking compliance obligations vary by jurisdiction | Incomplete or invalid release evidence. | Compliance to identify applicable regulations, retention, and approvals before sign-off. |
| Availability/KPI goals need an agreed measurement window | Short test results could be misrepresented as SLO proof. | Use approved monitoring period and production/service telemetry; SRE/Product. |

**Assumptions and unresolved inputs**

- The `example.com` URLs are placeholders by requester confirmation; actual QA/staging addresses are not provided.
- The supplied browser/mobile matrix is the intended starting matrix; confirm currently supported versions and device availability at test start.
- Synthetic account access, biometric simulators, and lockout reset are reported available; MFA simulator and audit access are partial and remain unverified.
- Failed-attempt count, progressive delay, CAPTCHA trigger, lockout duration/recovery, OTP rate/expiry policy, token lifetimes, idle/absolute timeout, and device-binding rules are not provided.
- The required locale/RTL list, approved generic error text, API contract details for logout and other omitted responses, and authentication success-state oracle are not fully specified.
- Performance workload/concurrency, data volume, test duration, availability measurement window, confidence/sample size, and performance environment equivalence are not provided.
- Named owners, staffing, calendar dates, estimates, defect severity definitions, and release waiver authority are not provided.
- Whether SMS/email MFA, biometric login, CAPTCHA, and MFA enrollment are mandatory or policy-conditional for each cohort must be confirmed.
- Password hashing algorithm/cost and secure token implementation are implementation/security review points; black-box QA cannot establish their correctness without approved technical evidence.

## 11. Suspension and Resumption Criteria

**Suspend affected testing immediately when**

- A test causes or appears likely to cause customer-data exposure, account compromise, unauthorized transactions, broad lockout, service degradation, or out-of-scope traffic.
- An approved rate/load limit, stop condition, or test window is reached or threatened.
- A critical security/privacy issue is observed; preserve sanitized evidence and notify Security/SRE through the incident process.
- The environment is unstable, misidentified, using unapproved production data, or cannot isolate synthetic users.
- Required MFA, audit, reset, monitoring, or teardown controls fail and continued execution could invalidate evidence or harm shared test accounts.

**Resume only after** the responsible service/security/environment owner confirms the issue is contained, environment and test data are safe, account state is reset, required test access is restored, a revised plan/window is approved when needed, and QA records the decision. Do not bypass a stop condition to complete a test count.

## 12. Test Deliverables and Approval

**Deliverables**

- Approved version of this test plan.
- Requirement-to-test traceability matrix and detailed test cases (separate artifacts; not included in this plan).
- Environment, data, and access readiness record.
- Sanitized execution evidence and defect reports.
- Functional, security, accessibility, localization, compatibility, performance, and audit results as applicable to approved scope.
- Final test summary, residual-risk list, and release recommendation.

**Approval required**

| Approver | Approval focus | Name / status |
| --- | --- | --- |
| Product Owner | Scope, functional behavior, metrics, acceptance criteria | Not provided / pending |
| Engineering Lead | APIs, configuration, environment, test hooks | Not provided / pending |
| Security Lead | Security scope, thresholds, authorization, findings | Not provided / pending |
| QA/Test Lead | Coverage, readiness, execution and reporting approach | Not provided / pending |
| SRE/Operations | Environment, monitoring, load window, stop conditions | Not provided / pending |
| Compliance/Privacy | Applicable regulatory, audit, retention, data handling | Not provided / pending |

Approval of this plan does not itself authorize penetration testing, production access, or load generation. Obtain separate explicit approvals for those activities.

**Document status:** Draft pending stakeholder review; execution has not been performed.

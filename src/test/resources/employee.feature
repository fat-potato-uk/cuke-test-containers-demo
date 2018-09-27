Feature: Employees can be created and queried

  Scenario: client sends a request to query existing employees
    When the database has been seeded
    And the client calls /employees
    And response code received is 200
    Then the client receives employees:
      | Bilbo Baggins |
      | Frodo Baggins |
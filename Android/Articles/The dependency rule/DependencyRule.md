# The Depenedency Rule

<img title="" src="https://khalilstemmler.com/img/wiki/dependency-rule/clean-architecture-layers.svg" alt="" data-align="left" width="657">

A software architecture rule that specifies the relationship between layers, namely that an inner layer should never rely on anything from an outer layer.

That rule specifies that something declared in an outer circle <u>must not be mentioned in the code by an inner circle</u>.

That means that code dependencies can only point inwards.
